import vuePlugin from 'eslint-plugin-vue';
import prettierPlugin from 'eslint-plugin-prettier';
import vueParser from 'vue-eslint-parser';
import babelParser from '@babel/eslint-parser';

export default [
  {
    files: ['**/*.js', '**/*.vue'],
    languageOptions: {
      parser: vueParser,
      parserOptions: {
        parser: babelParser,
        requireConfigFile: false,
        ecmaVersion: 'latest',
        sourceType: 'module',
      },
    },
    plugins: {
      vue: vuePlugin,
      prettier: prettierPlugin,
    },
    rules: {
      'vue/multi-word-component-names': 'off',
      'prettier/prettier': 'error',
    },
  },
];
