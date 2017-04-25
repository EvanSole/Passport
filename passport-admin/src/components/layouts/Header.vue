<template>
  <div id="layout-header">
        <header class="main-header">
			  <!-- Logo -->
			  <a href="#" class="logo">
			    <!-- mini logo for sidebar mini 50x50 pixels -->
			    <span class="logo-mini"><b>P</b>ass</span>
			    <!-- logo for regular state and mobile devices -->
			    <span class="logo-lg"><b>Pass</b>port</span>
			  </a>

			  <!-- Header Navbar -->
			  <nav class="navbar navbar-static-top" role="navigation">
				    <!-- Sidebar toggle button-->
				    <!--<a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">-->
				      <!--<span class="sr-only">Toggle navigation</span>-->
				    <!--</a>-->
					<layout-navbar></layout-navbar>

			  </nav>

		</header>
  </div>
</template>

<script>
import layoutNavbar from 'components/layouts/Navbar'
export default {
  components: {
    layoutNavbar
  }
}
</script>
