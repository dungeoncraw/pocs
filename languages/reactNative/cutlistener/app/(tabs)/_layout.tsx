import {Tabs} from "expo-router";
import {FontAwesome} from "@expo/vector-icons";

export default function TabLayout() {
    return (
        <Tabs screenOptions={{tabBarActiveTintColor: 'blue'}}>
            <Tabs.Screen name="index" options={{
                title: 'Home',
                headerShown: false,
                tabBarIcon: ({color}) => <FontAwesome size={28} name="home" color={color}/>,
            }}
            />
            <Tabs.Screen name="settings" options={{
                title: 'Settings',
                headerShown: false,
                tabBarIcon: ({ color }) => <FontAwesome size={28} name="cog" color={color} />,
            }}/>
        </Tabs>
    )
}