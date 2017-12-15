function inicializeJS(document){
	//iniciamos el contador de paginas a uno
	$("input[name='inputpage']").val(Number(1));

	function buildPagination(pageNumber){
		var stringPagination ='<li><a href="#" aria-label="Previous"><span aria-hidden="true">&laquo;</span></a></li>';
		
		if(pageNumber==1 || pageNumber==2) {
			if(pageNumber==1) {
				$('a[aria-label="Previous"]').addClass("disabled");
				stringPagination += '<li class="active"><a href="#">1</a></li>';
				stringPagination += '<li><a href="#">2</a></li>';
			} else {
				$('a[aria-label="Previous"]').removeClass("disabled");
				stringPagination += '<li><a href="#">1</a></li>';
				stringPagination += '<li class="active"><a href="#">2</a></li>';
			}
			stringPagination += '<li><a href="#">3</a></li><li><a href="#">4</a></li><li><a href="#">5</a></li>';
		}

		if(pageNumber>=3){
			$('a[aria-label="Previous"]').removeClass("disabled");
			stringPagination += '<li><a href="#">'+minus(pageNumber,2)+'</a></li>'
			stringPagination += '<li><a href="#">'+minus(pageNumber,1)+'</a></li>'
			stringPagination += '<li class="active"><a href="#">'+(pageNumber)+'</a></li>'
			stringPagination += '<li><a href="#">'+plus(pageNumber,1)+'</a></li>'
			stringPagination += '<li><a href="#">'+plus(pageNumber,2)+'</a></li>'
		}
		
		stringPagination += '<li><a href="#" aria-label="Next"><span aria-hidden="true">&raquo;</span></a></li>';
		$('ul[class="pagination"]').append(stringPagination);
		$("input[name='inputpage']").val(pageNumber);
	}
	
	//peticion de busqueda en solar
	function getSearch(page) {
		var searchwords = $("input[name='inputsearch']").val();
		if(searchwords!="") {
			$("#loading").removeClass("hidden");
			$("#seacher-results").empty();
			$('ul[class="pagination"]').empty();
			$.getJSON("http://localhost:8983/solr/collection1/select?q=\""+searchwords+"\"&start="+((page-1)*10)+"&rows=10&wt=json&df=title&sort=tstamp+desc", function(data) {
				
				if(data.response.numFound==0) {
					$("#seacher-results").append('<dt>Ups, lo sentimos</dt><dd>No se han encontrado resultados para la búsqueda introducida</dd>');
				} else {
					$.each(data.response.docs, function(index, item) {
						$("#seacher-results").append('<dt><a href="'+item.url+'">'+item.title+'</a></dt><dd>'+item.description+'</dd>');
					});
					
					buildPagination(page);
					$("#pagination").removeClass("hidden");
				}
				$(document).scrollTop(0);
			});
		}	
	}
	
	//boton buscar
	$("button").click(function(){
		getSearch(1);
	});
	
	//botones de paginacion
	$('body').delegate( 'ul li a', "click", function() {
		$("#pagination").addClass("hidden");
		var paginationClick = $(this).attr("aria-label");
		if(undefined != paginationClick){
			if(paginationClick == "Previous" && $(this).hasClass("disabled")) {
				getSearch(minus(Number($("input[name='inputpage']").val()),1));
			} else {
				getSearch(plus(Number($("input[name='inputpage']").val()),1));
			}
		} else {
			getSearch(Number($(this).text()));
		}
		
	});
	
	
	function plus(num, plus){
		return Number(num) + Number(plus);
	}
	
	function minus(num, minus){
		return Number(num) - Number(minus);
	}
}	
	
