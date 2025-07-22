import { FlatList, StyleSheet, Text, View, TouchableOpacity } from 'react-native';
import { FontAwesome } from '@expo/vector-icons';
import {ListComponentProps, ListItemProps} from "@/app/types/types";


const ListItem = ({ item, onPress }: { item: ListItemProps; onPress: () => void }) => {
    console.log('itemmm', item);
    return (
    <TouchableOpacity style={styles.itemContainer} onPress={onPress}>
        <View style={styles.itemContent}>
            <Text style={styles.title}>ahhhhhhhhhhhhhhhh{item.id}</Text>
        </View>
        <FontAwesome name="chevron-right" size={16} color="#97a3b4" />
    </TouchableOpacity>
)};

export default function ListComponent({ data, onItemPress }: ListComponentProps) {
    const renderItem = ({ item }: { item: ListItemProps }) => (
        <ListItem item={item} onPress={() => onItemPress(item)} />
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
        height: 80,
        width: '100%',
    },
    itemContent: {
        flex: 1,
        width: '100%',
    },
    title: {
        fontSize: 16,
        fontFamily: 'Roboto',
        fontWeight: 'bold',
        marginBottom: 4,
        width: '80%'
    },
    description: {
        fontSize: 14,
        fontFamily: 'Roboto',
        color: '#666',
    },
});