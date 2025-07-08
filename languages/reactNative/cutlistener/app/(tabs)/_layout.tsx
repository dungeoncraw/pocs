import {Tabs} from "expo-router";
import {FontAwesome} from "@expo/vector-icons";

export default function TabLayout() {
    return (
        <Tabs 
            testID="tabs"
            screenOptions={{tabBarActiveTintColor: 'blue'}}
        >
            <Tabs.Screen 
                testID="tab-screen-index"
                name="index" 
                options={{
                    title: 'Home',
                    headerShown: false,
                    tabBarIcon: ({color}) => <FontAwesome size={28} name="home" color={color}/>,
                }}
            />
            <Tabs.Screen 
                testID="tab-screen-settings"
                name="settings" 
                options={{
                    title: 'Settings',
                    headerShown: false,
                    tabBarIcon: ({ color }) => <FontAwesome size={28} name="cog" color={color} />,
                }}
            />
        </Tabs>
    )
}
