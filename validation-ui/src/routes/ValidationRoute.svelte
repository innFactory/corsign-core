<script lang="ts">
  import { DoubleBounce } from 'svelte-loading-spinners'
  import CheckmarkFilled32 from "carbon-icons-svelte/lib/CheckmarkFilled32";
  import CheckboxIndeterminateFilled32 from "carbon-icons-svelte/lib/CheckboxIndeterminateFilled32";
  
  import { claim_component } from 'svelte/internal';
  
  export let currentRoute
  const apiEndpoint = API_ENDPOINT

  let result = null

  async function validate () {
		const res = await fetch(apiEndpoint+'/validate/'+currentRoute.namedParams.signer+'/'+currentRoute.namedParams.token, {
			method: 'GET'
		})
		const json = await res.json()
		result = json
	}

  validate()
</script>

{#if !result}
<div style="text-align:center;">
  QR Code wird validiert.
</div>
{:else}
  {#if result && result.verifiedContent.claims.isNegative}
  <img src="../../images/check.svg" alt="Negativ" />
  <p>{result.text}</p>
  {:else if result && !result.verifiedContent.claims.isNegative}
  <img src="../../images/close.svg" alt="Positiv" />
  <p>{result.text}</p>
  {:else}
  Der Token ist nicht mehr gültig
  {/if}
{/if}



<style lang="scss">
  p {
    padding-top: 15px;
  }
  img {
    max-width: 500px;
  }
</style>
