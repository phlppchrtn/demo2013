<html>
  </head>

	 <head>
	    <meta charset="utf-8">
	    <title>${note.datas.title}</title>
	    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	    <meta name="description" content="${note.datas.description}">
	    <meta name="author" content="${note.datas.author}">
	
	    <!-- Le styles -->
		<link href="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/css/bootstrap-combined.min.css" rel="stylesheet">
		<script src="http://netdna.bootstrapcdn.com/twitter-bootstrap/2.2.2/js/bootstrap.min.js"></script>
	
	    <!-- Fav and touch icons -->
	    <link rel="shortcut icon" href="assets/ico/favicon.png">
	    <style>
	    	body {
				padding-top: 60px; /* 60px to make the container go all the way to the bottom of the topbar */
				background-color: #fff;
			}
		</style>	
	</head>

	<body>
		 <div class="navbar navbar-inverse navbar-fixed-top">
	     	<div class="navbar-inner">
	        	<div class="container">
	          		<a class="brand" href="#">Project name</a>
	          		<div class="nav-collapse collapse">
	            		<ul class="nav">
 	            		<#list site.categories as category>	
	              			<li class="active"><a href="#">${category}</a></li>
	            		</#list>
	            		</ul>
	          		</div><!--/.nav-collapse -->
	        	</div><!--container-->
	    	</div><!--navbar-inner-->
	    </div>
	    
 		<div class="container">
	    <ul class="breadcrumb">
		      <li><a href="#">Home</a> <span class="divider">/</span></li>
		   	<#list note.categories as category>
			  <li><a href="#">${category}</a> <span class="divider">/</span></li>
		  	</#list>
		</ul>

  			${content}
			<!--
				<div class="row">
	        		<div class="span4">
	        	</div>
			-->
			
			<div id="footer">
				<ul class="nav nav-pills">
					<#list note.datas.tags as tag>
					<li class="active"><a href="#">${tag}</a></li>
					</#list>
				</ul>
			</div>
		</div><!--container-->
	</body>
</html>