import React from 'react';
import { render } from '@testing-library/react-native';
import Separator from '@/app/component/Separator';

describe('Separator', () => {
  it('renders correctly', () => {
    const { toJSON } = render(<Separator />);

    // Snapshot test to verify the component renders correctly
    expect(toJSON()).toMatchSnapshot();
  });

  it('has the correct style', () => {
    const { getByTestId } = render(<Separator testID="separator" />);

    const separator = getByTestId('separator');

    // Check that the separator has the correct style
    expect(separator.props.style).toMatchObject({
      height: 1,
      backgroundColor: '#014898',
      marginVertical: 10,
      width: '100%',
    });
  });
});
