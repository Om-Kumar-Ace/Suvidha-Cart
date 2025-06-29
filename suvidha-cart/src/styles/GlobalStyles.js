// src/styles/GlobalStyles.js
import { Global, css } from '@emotion/react';
import { useTheme } from '../contexts/ThemeContext';

const GlobalStyles = () => {
  const { theme } = useTheme();

  return (
    <Global
      styles={css`
        body {
          margin: 0;
          font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Oxygen',
            'Ubuntu', 'Cantarell', 'Fira Sans', 'Droid Sans', 'Helvetica Neue',
            sans-serif;
          -webkit-font-smoothing: antialiased;
          -moz-osx-font-smoothing: grayscale;
          background-color: ${theme.palette.background.default};
          color: ${theme.palette.text.primary};
          transition: all 0.3s ease;
        }

        code {
          font-family: source-code-pro, Menlo, Monaco, Consolas, 'Courier New',
            monospace;
        }

        a {
          color: ${theme.palette.primary.main};
          text-decoration: none;
          &:hover {
            text-decoration: underline;
          }
        }

        .MuiPaper-root {
          background-color: ${theme.palette.background.paper};
          color: ${theme.palette.text.primary};
        }
      `}
    />
  );
};

export default GlobalStyles;