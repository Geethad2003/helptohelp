/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./src/**/*.{html,ts}"],
  theme: {
    extend: {
      colors: {
        brand: {
          start: "#3b82f6", // blue-500
          end: "#10b981",   // emerald-500
        },
      },
      boxShadow: {
        soft: "0 20px 45px rgba(16,185,129,0.15)",
      },
      borderRadius: {
        "3xl": "1.5rem",
      },
    },
  },
  plugins: [],
};
