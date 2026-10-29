import pluginVue from 'eslint-plugin-vue'
import tsPlugin from '@typescript-eslint/eslint-plugin'
import tsParser from '@typescript-eslint/parser'
export default [
  {ignores:['dist/**','node_modules/**','test-results/**','playwright-report/**']},
  ...pluginVue.configs['flat/recommended'],
  {files:['**/*.ts'],languageOptions:{parser:tsParser},plugins:{'@typescript-eslint':tsPlugin},rules:tsPlugin.configs.recommended.rules},
  {files:['**/*.vue'],languageOptions:{parserOptions:{parser:tsParser,extraFileExtensions:['.vue']}},plugins:{'@typescript-eslint':tsPlugin},rules:{...tsPlugin.configs.recommended.rules,'vue/max-attributes-per-line':'off','vue/singleline-html-element-content-newline':'off','vue/html-self-closing':'off'}},
]
