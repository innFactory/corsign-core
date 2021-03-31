<script lang="ts">
  import { Header, SkipToContent } from "carbon-components-svelte";
  import { getContext } from "svelte";

  const ctx: { dark: any; light: any; updateVar: any } = getContext("Theme");
  $: if (ctx) {
    ctx.dark.subscribe((value) => {
      console.log("dark mode?", value);
    });
    ctx.light.subscribe((value) => {
      console.log("light mode?", value);
    });
    ctx.updateVar("--cds-productive-heading-06-font-size", "4rem");
  }

  export let isSideNavOpen = false;
</script>

<Header
  company="Corsign"
  platformName="Validation App"
  href="/"
  bind:isSideNavOpen
>
<!--persistentHamburgerMenu-->
  <div slot="skip-to-content">
    <SkipToContent href="/todo" tabindex="3" />
  </div>
</Header>
