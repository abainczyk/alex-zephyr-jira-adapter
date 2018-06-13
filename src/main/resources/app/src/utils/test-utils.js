export class TestUtils {

  static createTestStepFromSymbol(symbol) {
    return {
      shouldFail: false,
      symbol: {
        id: symbol.id,
        name: symbol.name
      },
      parameterValues: symbol.inputs
        .filter(input => input.parameterType === 'STRING')
        .map(input => ({parameter: input, value: ''}))
    };
  }

}
