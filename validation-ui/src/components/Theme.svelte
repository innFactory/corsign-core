<script lang="ts">
  type Theme = "white" | "g10" | "g90" | "g100" | "demo";
  export let persist: boolean = false;
  export let persistKey: string = "theme";
  export let theme: Theme = "g10";
  export const themes: Theme[] = ["white", "g10", "g90", "g100", "demo"];
  import { afterUpdate, onMount, setContext } from "svelte";
  import { derived, writable } from "svelte/store";

  const isValidTheme = (value) => themes.includes(value);
  const isDark = (value) =>
    isValidTheme(value) && (value === "g90" || value === "g100");
  const dark = writable(isDark(theme));
  const light = derived(dark, (_) => !_);
  setContext("Theme", {
    updateVar: (name: string, value: string) => {
      document.documentElement.style.setProperty(name, value);
    },
    dark,
    light,
  });
  onMount(() => {
    try {
      const persisted_theme = localStorage.getItem(persistKey);
      if (isValidTheme(persisted_theme)) {
        theme = persisted_theme as Theme;
      }
    } catch (error) {
      console.error(error);
    }
  });
  afterUpdate(() => {
    if (isValidTheme(theme)) {
      console.log(theme);

      document.documentElement.setAttribute("theme", theme);
      if (persist) {
        localStorage.setItem(persistKey, theme);
      }
    } else {
      console.warn(
        `"${theme}" is not a valid Carbon theme. Choose from available themes: ${JSON.stringify(
          themes
        )}`
      );
    }
  });
  $: dark.set(isDark(theme));
</script>

<slot />
