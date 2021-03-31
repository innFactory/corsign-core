const sveltePreprocess = require("svelte-preprocess");

const production = !process.env.ROLLUP_WATCH;

const preprocessOptions = {
  sourceMap: !production,
  defaults: {
    script: "typescript",
    style: "scss",
  },
  scss: {
    includePaths: ["src/theme", "node_modules"],
  },
  postcss: {
    plugins: [require("autoprefixer")()],
  },
  css: (css) => {
    css.write("public/bundle.css");
  },
  compilerOptions: {
    // enable run-time checks when not in production
    dev: !production,
  },
};

module.exports = {
  preprocess: sveltePreprocess(preprocessOptions),

  // Export this to allow rollup.config.js to inherit the same preprocess options.
  preprocessOptions,
};
