import { CSV } from "./commands/command_utils";

/**
 * A command-processor function for our REPL. The function returns a Promise
 * which resolves to a string, which is the value to print to history when
 * the command is done executing.
 *
 * The arguments passed in the input (which need not be named "args") should
 * *NOT*contain the command-name prefix.
 */
export interface REPLFunction {
  (args: Array<string>): Promise<ValidREPLOutput>;
}


/**
 * The types of outputs acceptable from a REPLFunction. 
 * 
 * Can be freely modified by a developer user, but most update CommandLog 
 * component to handle new potential output type accordingly.
 */
export type ValidREPLOutput = string | CSV;

export class Repl {
  private readonly registered: Map<string, REPLFunction> = new Map();

  /**
   * Register new command that Repl can execute
   *
   * @param {string} name - string name user can type in to execute function
   * @param {REPLFunction} func - corresponding function that should be called 
   * when user types in the command name
   * @return true if and only if an old command was overwritten with a *new* command.
   * returns false if method attempts to overrite the SAME command
   */
  registerCommand(commandName: string, func: REPLFunction): boolean {
    const isOverwritten = (): boolean => {
      const registeredFunc: REPLFunction | undefined =
        this.registered.get(commandName);
      if (registeredFunc === undefined) {
        return false;
      }
      if (registeredFunc === func) {
        return false;
      }
      return true;
    };
    const result: boolean = isOverwritten();
    this.registered.set(commandName, func);
    return result;
  }

  /**
   * Strip double quotes that wrap args when splitting based on regex
   * 
   * @param {Array<string>} rawArgs the raw args to be processed
   * @returns strips any unnecessary quotes from the string
   */
  private stripWrapQuotes(rawArgs: Array<string>) {
    return rawArgs.map((arg) => {
      const endIndex: number = arg.length - 1;
      const hasWrapQuotes: boolean = arg[0] === '"' && arg[endIndex] === '"';
      return hasWrapQuotes ? arg.substring(1, endIndex) : arg;
    });
  }

  /**
   * Parse arguments based on whitespace given an input string. Able to parse
   * any string wrapped in *double quotes* as a single argument. 
   * 
   * @param inputStr the input string entered by the user
   * @returns input tokens split by the space character
   */
  private parseArgs(inputStr: string): Array<string> {
    const regex: RegExp = /(?:[^\s"]+|"[^"]*")+/g;
    const regexMatches: RegExpMatchArray | null = inputStr.match(regex);
    const args: Array<string> =
      regexMatches != null
        ? this.stripWrapQuotes(
            regexMatches.filter((n) => n != null || n === " ")
          )
        : [];
    return args;
  }

  /**
   *check whether command is in the registered map
   * 
   * @param commandName command enetered by the user
   * @returns a boolean that indicates if the command was registered or not
   */
  checkIsRegistered(commandName: string) {
    return this.registered.has(commandName);
  }

  /**
   * Runs a registered command returns the output in a promise
   * @param inputStr - the input string directly input by the user
   * @returns {Promise<ValidREPLOutput>} - a Promise containing the command output 
   * from the REPLFUnciont
   */
  async run(inputStr: string): Promise<ValidREPLOutput> {
    if (inputStr === "") {
      return "error: submitted empty string";
    }
    const inputTokens: Array<string> = this.parseArgs(inputStr);
    const [commandName, ...args] = inputTokens;
    const commandFunc: REPLFunction | undefined =
      this.registered.get(commandName);

    if (commandFunc === undefined) {
      return `error: command '${commandName}' not found`;
    }
    const output: ValidREPLOutput = await commandFunc(args);
    return output;
  }
}
