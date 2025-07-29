export enum PluginType {
    REACT_NATIVE_AUDIO_RECORDER_PLAYER = "RECORDER_PLAYER",
}

export interface Recordings {
    uri: string;
    name: string;
    duration?: number;
    size?: number;
}

export interface ListItemProps {
    id: string;
    title: string;
    recording: Recordings;
}

export interface ListComponentProps {
    data: ListItemProps[];
    onItemPress: (item: ListItemProps) => void;
    onDelete: (item: ListItemProps) => void;
}