<script lang="ts">
  import {
    Column,
    Content,
    Grid,
    Row,
    Select,
    SelectItem,
  } from "carbon-components-svelte";
  import { Router } from "svelte-router-spa";
  import Header from "./components/Header.svelte";
  import Theme from "./components/Theme.svelte";
  import { routes } from "./routes/routes";

  let theme: "g10" = "g10";
</script>

<Theme persist bind:theme>
  <Header/>
  <div class="content-wrapper">
    <Content style="transform: unset; margin-top: 48px; background: unset; text-align: center;">
            <Router {routes} />
    </Content>
  </div>
</Theme>

<style lang="scss" global>
  @import "./theme/main.scss";

  .content-wrapper {
    height: 100%;
  }

  .content-wrapper-nav-open {
    margin-left: 200px;
  }
</style>
