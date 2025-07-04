import React from 'react';
import { View, StyleSheet } from 'react-native';

const Separator = () => (
    <View style={styles.line} />
);

const styles = StyleSheet.create({
    line: {
        height: 1,
        backgroundColor: '#014898',
        marginVertical: 10,
        width: '100%',
    },
});

export default Separator;