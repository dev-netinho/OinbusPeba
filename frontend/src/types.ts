export type Leg={mode:'WALK'|'TRANSIT'|'TRANSFER';from:string;to:string;departure:string;arrival:string;line:string|null;direction:string|null;distanceMeters:number;provenance:string}
export type Journey={id:string;departure:string;arrival:string;durationMinutes:number;walkingMinutes:number;waitingMinutes:number;ridingMinutes:number;transfers:number;label:string;legs:Leg[]}
export type Vehicle={vehicleId:string;line:string;direction:string;latitude:number;longitude:number;nextStop:string;etaMinutes:number;calculatedAt:string;simulated:boolean}
