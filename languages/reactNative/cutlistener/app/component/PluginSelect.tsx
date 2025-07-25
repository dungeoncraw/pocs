import React, { useState } from 'react';
import { TouchableOpacity, Modal, FlatList, Text, View, StyleSheet } from 'react-native';
import { PluginType } from "@/app/types/types";

interface SelectProps {
    data: { id: PluginType; label: string }[];
    onSelect: (item: { id: PluginType; label: string }) => void;
    placeholder?: string;
    selectedItem?: { id: PluginType; label: string } | null;
}

const PluginSelect: React.FC<SelectProps> = ({
                                           data,
                                           onSelect,
                                           placeholder = 'Select an option',
                                           selectedItem = null,
                                       }) => {
    const [modalVisible, setModalVisible] = useState(false);

    const handleSelect = (item: { id: PluginType; label: string }) => {
        onSelect(item);
        setModalVisible(false);
    };

    return (
        <View>
            <TouchableOpacity
                style={styles.selectButton}
                onPress={() => setModalVisible(true)}
            >
                <Text style={styles.selectButtonText}>
                    {selectedItem ? selectedItem.label : placeholder}
                </Text>
            </TouchableOpacity>

            <Modal
                visible={modalVisible}
                animationType="slide"
                transparent={true}
                onRequestClose={() => setModalVisible(false)}
            >
                <View style={styles.modalContainer}>
                    <View style={styles.modalContent}>
                        <TouchableOpacity
                            style={styles.closeButton}
                            onPress={() => setModalVisible(false)}
                        >
                            <Text style={styles.closeButtonText}>Close</Text>
                        </TouchableOpacity>

                        <FlatList
                            data={data}
                            keyExtractor={(item) => item.id.toString()}
                            renderItem={({ item }) => (
                                <TouchableOpacity
                                    style={styles.optionItem}
                                    onPress={() => handleSelect(item)}
                                >
                                    <Text style={styles.optionText}>{item.label}</Text>
                                </TouchableOpacity>
                            )}
                        />
                    </View>
                </View>
            </Modal>
        </View>
    );
};

const styles = StyleSheet.create({
    selectButton: {
        padding: 12,
        borderWidth: 1,
        borderColor: '#ccc',
        borderRadius: 8,
        backgroundColor: '#fff',
        marginBottom: 10,
    },
    selectButtonText: {
        fontSize: 16,
        color: '#333',
    },
    modalContainer: {
        flex: 1,
        justifyContent: 'flex-end',
        backgroundColor: 'rgba(0, 0, 0, 0.5)',
    },
    modalContent: {
        backgroundColor: '#fff',
        borderTopLeftRadius: 20,
        borderTopRightRadius: 20,
        paddingBottom: 20,
        maxHeight: '80%',
    },
    closeButton: {
        padding: 15,
        alignItems: 'center',
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    closeButtonText: {
        fontSize: 16,
        color: '#007AFF',
    },
    optionItem: {
        padding: 15,
        borderBottomWidth: 1,
        borderBottomColor: '#eee',
    },
    optionText: {
        fontSize: 16,
        color: '#333',
    },
});

export default PluginSelect;
