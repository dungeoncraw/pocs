import React from 'react';
import { render } from '@testing-library/react-native';
import RootLayout from '@/app/_layout';

// Mock dependencies
jest.mock('expo-router', () => {
  const StackComponent = function Stack({ children }) {
    return <div data-testid="stack">{children}</div>;
  };

  StackComponent.Screen = function Screen({ name, options }) {
    return <div data-testid={`stack-screen-${name}`} data-options={JSON.stringify(options)} />;
  };

  return {
    Stack: StackComponent
  };
});

jest.mock('jotai', () => ({
  Provider: function Provider({ children }) {
    return <div data-testid="jotai-provider">{children}</div>;
  }
}));

describe('RootLayout', () => {
  it('renders a component', () => {
    const { root } = render(<RootLayout />);
    expect(root).toBeTruthy();
  });
});
