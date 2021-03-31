import HomeRoute from "./HomeRoute.svelte";
import ValidationRoute from "./ValidationRoute.svelte";

const routes = [
  {
    name: "/",
    component: HomeRoute,
  },
  {
    name: "/validate/:signer/:token",
    component: ValidationRoute,
  },
];

export { routes };
