import React from 'react';
import { render } from '@testing-library/react-native';
import TabLayout from '@/app/(tabs)/_layout';

// Mock dependencies
jest.mock('expo-router', () => {
  const TabsComponent = function Tabs({ children, screenOptions }) {
    return (
      <div data-testid="tabs" data-screen-options={JSON.stringify(screenOptions)}>
        {children}
      </div>
    );
  };

  TabsComponent.Screen = function Screen({ name, options }) {
    return (
      <div 
        data-testid={`tab-screen-${name}`} 
        data-options={JSON.stringify(options)} 
      />
    );
  };

  return {
    Tabs: TabsComponent
  };
});

jest.mock('@expo/vector-icons', () => ({
  FontAwesome: jest.fn(({ size, name, color }) => (
    <div 
      data-testid={`icon-${name}`} 
      data-size={size} 
      data-color={color} 
    />
  ))
}));

describe('TabLayout', () => {
  it('renders a component', () => {
    const { root } = render(<TabLayout />);
    expect(root).toBeTruthy();
  });
});
