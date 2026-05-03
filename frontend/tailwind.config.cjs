module.exports = {
  content: ['./index.html', './src/**/*.{vue,js,ts,jsx,tsx}'],
  theme: {
    extend: {
      colors: {
        'gamer-night': '#0b1120',
        'gamer-void': '#020617',
        'gamer-gold': '#fbbf24'
      },
      fontFamily: {
        display: ['Orbitron', 'ui-sans-serif', 'system-ui'],
        body: ['Exo 2', 'ui-sans-serif', 'system-ui']
      }
    }
  },
  plugins: []
}
