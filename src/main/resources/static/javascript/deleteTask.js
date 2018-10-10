(function($) {
	var pageOpt = {};
	pageOpt.page=1;
	pageOpt.fetchData = function(){
		$.ajax({
			url:"/framework/findDeleteTask",
			type:"post",
			data:{
				page:pageOpt.page,
				rows:20
			},
			success: function(res){
				if(res.success && res.data.data.length>0){
					var tpl = $("#list_tpl").html();
					var html = juicer(tpl, {d:res.data});
					$("#grid_table").html(html);
				}
			}
		});
	}
	
	pageOpt.events = function(){
		
	}
	pageOpt.pageToolbar = function(){
		$("#grid_table").on("click", ".page-toolbar a", function(){
			var page=$(this).data("page");
			if(!page || page*1<1){
				return;
			}
			pageOpt.page=page;
			pageOpt.fetchData();
		})
	}
	
	pageOpt.fetchData();
	pageOpt.events();
	pageOpt.pageToolbar();
})(jQuery);