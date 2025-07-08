import React from 'react';
import { render, fireEvent } from '@testing-library/react-native';
import PluginSelect from '@/app/component/PluginSelect';
import { PluginType } from '@/app/types/types';

describe('PluginSelect', () => {
  const mockData = [
    { id: PluginType.REACT_NATIVE_AUDIO_RECORDER_PLAYER, label: 'React Native Audio Recorder Player' },
    { id: PluginType.EXPO_AUDIO, label: 'Expo Audio' }
  ];
  
  const mockOnSelect = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders correctly with default placeholder', () => {
    const { getByText } = render(
      <PluginSelect data={mockData} onSelect={mockOnSelect} />
    );
    
    // Check that the default placeholder is rendered
    expect(getByText('Selecione uma opção')).toBeTruthy();
  });

  it('renders correctly with custom placeholder', () => {
    const { getByText } = render(
      <PluginSelect 
        data={mockData} 
        onSelect={mockOnSelect} 
        placeholder="Custom Placeholder" 
      />
    );
    
    // Check that the custom placeholder is rendered
    expect(getByText('Custom Placeholder')).toBeTruthy();
  });

  it('renders correctly with selected item', () => {
    const selectedItem = mockData[0];
    const { getByText } = render(
      <PluginSelect 
        data={mockData} 
        onSelect={mockOnSelect} 
        selectedItem={selectedItem} 
      />
    );
    
    // Check that the selected item label is rendered
    expect(getByText(selectedItem.label)).toBeTruthy();
  });

  it('opens modal when select button is pressed', () => {
    const { getByText, queryByText } = render(
      <PluginSelect data={mockData} onSelect={mockOnSelect} />
    );
    
    // Modal should not be visible initially
    expect(queryByText('Fechar')).toBeNull();
    
    // Press the select button
    fireEvent.press(getByText('Selecione uma opção'));
    
    // Modal should be visible now
    expect(getByText('Fechar')).toBeTruthy();
    
    // Options should be visible in the modal
    expect(getByText('React Native Audio Recorder Player')).toBeTruthy();
    expect(getByText('Expo Audio')).toBeTruthy();
  });

  it('calls onSelect and closes modal when an option is selected', () => {
    const { getByText } = render(
      <PluginSelect data={mockData} onSelect={mockOnSelect} />
    );
    
    // Open the modal
    fireEvent.press(getByText('Selecione uma opção'));
    
    // Select an option
    fireEvent.press(getByText('Expo Audio'));
    
    // Check that onSelect was called with the correct item
    expect(mockOnSelect).toHaveBeenCalledWith(mockData[1]);
    
    // Modal should be closed now
    expect(() => getByText('Fechar')).toThrow();
  });

  it('closes modal when close button is pressed', () => {
    const { getByText } = render(
      <PluginSelect data={mockData} onSelect={mockOnSelect} />
    );
    
    // Open the modal
    fireEvent.press(getByText('Selecione uma opção'));
    
    // Press the close button
    fireEvent.press(getByText('Fechar'));
    
    // Modal should be closed now
    expect(() => getByText('Fechar')).toThrow();
    
    // onSelect should not have been called
    expect(mockOnSelect).not.toHaveBeenCalled();
  });
});