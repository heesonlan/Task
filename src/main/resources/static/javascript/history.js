(function($) {
	var pageOpt = {};
	pageOpt.page=1;
	pageOpt.fetchData = function(){
		if(!pageOpt.tag){
			pageOpt.tag=pageOpt.getQueryString("tag");
		}
		var jobId=pageOpt.getQueryString("jobId");
		$.ajax({
			url:"/framework/findHistory",
			type:"post",
			data:{
				page:pageOpt.page,
				rows:20,
				type:pageOpt.tag,
				jobId:jobId?jobId:0
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
	
	//从url中获取某个参数值，如：www.xx.com?bid=10;通过传入bid获取10。
	pageOpt.getQueryString = function (name) {
		var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i"); 
		var r = window.location.search.substr(1).match(reg); 
		if (r != null)
			return unescape(r[2]);
		return null; 
	};
	
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