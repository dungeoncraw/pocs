import React from 'react';
import { View, StyleSheet, ViewProps } from 'react-native';

interface SeparatorProps extends ViewProps {
    testID?: string;
}

const Separator = ({ testID = 'separator', ...props }: SeparatorProps) => (
    <View style={styles.line} testID={testID} {...props} />
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
