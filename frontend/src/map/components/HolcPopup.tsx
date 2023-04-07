import mapboxgl from "mapbox-gl";

export interface PointPopupProps {
    lat: number,
    lon: number,
    name: string
    grade: string,
    state: string,
    city: string,
    desc: any
}

function getPopupContent(props: PointPopupProps): string {
    console.log(props.desc);
    return `<div className="">
                <p className="name">${props.name === undefined ? "Unnamed Neighborhood" : props.name}</p>
                <p className="grade">Grade: ${props.grade}</p>
                <p className="location">${props.city}, ${props.state}</p>
            </div>`;
}

export default function HolcPopup(props: PointPopupProps) {
    return new mapboxgl.Popup().setLngLat([props.lon, props.lat]).setHTML(getPopupContent(props));
}