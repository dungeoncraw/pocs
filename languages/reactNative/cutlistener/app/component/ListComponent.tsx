import { FlatList, StyleSheet, Text, View, TouchableOpacity } from 'react-native';
import { FontAwesome } from '@expo/vector-icons';
import {ListComponentProps, ListItemProps} from "@/app/types/types";


const ListItem = ({ 
    item, 
    onPress, 
    onPlay, 
    onDelete 
}: { 
    item: ListItemProps; 
    onPress: () => void;
    onPlay: () => void;
    onDelete: () => void;
}) => {
    return (
        <TouchableOpacity style={styles.itemContainer} onPress={onPress}>
            <View style={styles.itemContent}>
                <Text style={styles.title}>{item.recording.name}</Text>
                <View style={styles.buttonRow}>
                    <TouchableOpacity 
                        style={[styles.actionButton, styles.playButton]} 
                        onPress={(e) => {
                            e.stopPropagation();
                            onPlay();
                        }}
                    >
                        <FontAwesome name="play" size={16} color="#1ac10d" />
                        <Text style={styles.buttonText}>Play</Text>
                    </TouchableOpacity>
                    
                    <TouchableOpacity 
                        style={[styles.actionButton, styles.deleteButton]} 
                        onPress={(e) => {
                            e.stopPropagation();
                            onDelete();
                        }}
                    >
                        <FontAwesome name="trash" size={16} color="#c10d10" />
                        <Text style={styles.buttonText}>Delete</Text>
                    </TouchableOpacity>
                </View>
            </View>
        </TouchableOpacity>
    );
};

export default function ListComponent({ data, onItemPress, onDelete }: ListComponentProps) {
    const renderItem = ({ item }: { item: ListItemProps }) => (
        <ListItem 
            item={item} 
            onPress={() => onItemPress(item)}
            onPlay={() => onItemPress(item)}
            onDelete={() => onDelete(item)}
        />
    );
    
    return (
        <FlatList
            data={data}
            renderItem={renderItem}
            keyExtractor={(item) => item.id}
            contentContainerStyle={styles.listContainer}
            showsVerticalScrollIndicator={false}
        />
    );
}

const styles = StyleSheet.create({
    listContainer: {
        padding: 16,
        flex: 1,
    },
    itemContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        padding: 16,
        backgroundColor: '#fff',
        borderRadius: 10,
        marginBottom: 8,
        shadowColor: '#000',
        shadowOffset: {
            width: 0,
            height: 2,
        },
        shadowOpacity: 0.1,
        shadowRadius: 3.84,
        elevation: 5,
        minHeight: 100,
        width: '100%',
    },
    itemContent: {
        width: '100%',
        flexDirection: 'column',
    },
    title: {
        fontSize: 16,
        fontFamily: 'Roboto',
        fontWeight: 'bold',
        marginBottom: 12,
        color: '#333',
    },
    buttonRow: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: 12,
    },
    actionButton: {
        flexDirection: 'row',
        alignItems: 'center',
        paddingHorizontal: 16,
        paddingVertical: 8,
        borderRadius: 20,
        flex: 1,
        justifyContent: 'center',
        gap: 6,
    },
    playButton: {
        backgroundColor: '#e8f5e8',
        borderWidth: 1,
        borderColor: '#1ac10d',
    },
    deleteButton: {
        backgroundColor: '#fce8e8',
        borderWidth: 1,
        borderColor: '#c10d10',
    },
    buttonText: {
        fontSize: 14,
        fontFamily: 'Roboto',
        fontWeight: '500',
    },
});