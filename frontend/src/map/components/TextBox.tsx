
export interface TextBoxProps {
    header: String,
    content: String[];
}
export default function Text(props: TextBoxProps) {
    return (
        <div className="output-box">
            {props.header ? (<h2>{props.header}</h2>): null}
            {props.content.map((s: String) => 
                (<p>{s}</p>)
            )}
        </div>
    )
}