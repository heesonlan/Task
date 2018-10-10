(function($) {
	var pageOpt = {};
	pageOpt.page=1;
	pageOpt.fetchData = function(){
		$.ajax({
			url:"/framework/findSuspendTask",
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
		//***********************************更新cron*****************************************//
		$("#grid_table").on("click", ".action-update-cron", function(){
			var jobId=$(this).parent("td").data("id");
			layer.prompt({title: '请输入CRON表达式', formType: 2}, function(cron, index){
				var valid = cronValidate(cron);
				if(valid!=true){
					layer.msg(valid);
					return;
				}
				$.ajax({
					url:"/framework/updateCron",
					type:"post",
					data:{
						jobId:jobId,
						cron:cron
					},
					success: function(res){
						if(res.success){
							layer.close(index);
							layer.msg("操作成功");
							pageOpt.fetchData();
						}else{
							layer.alert(res.message, {
							  skin: 'layui-layer-molv', //样式类名
							  closeBtn: 0
							});
						}
					}
				})
			});
		});
		
		//***********************************生效失效*****************************************//
		$("#grid_table").on("click", ".action-change-status", function(){
			var jobId=$(this).parent("td").data("id");
			var status=$(this).html();
			var content="";
			if(status=="使生效"){
				status="01";
				content="任务将根据cron表达式运行";
			}else if(status="使失效"){
				status="02";
				content="任务将暂停运行";
			}else{
				return;
			}
			layer.confirm(content, {
			  	btn: ['确定','取消'] //按钮
			}, function(){
				$.ajax({
					url:"/framework/changeStatus",
					type:"post",
					data:{
						jobId:jobId,
						status:status
					},
					success: function(res){
						if(res.success){
							layer.msg("操作成功");
							pageOpt.fetchData();
						}else{
							layer.alert(res.message, {
							  skin: 'layui-layer-molv', //样式类名
							  closeBtn: 0
							});
						}
					}
				})
			})

		});
		
		//***********************************删除*****************************************//
		$("#grid_table").on("click", ".action-del", function(){
			var jobId=$(this).parent("td").data("id");
			layer.confirm('确定删除该任务，删除后任务将不会再运行，并从可运行列表中移除', {
			  	btn: ['确定','取消'] //按钮
			}, function(){
				$.ajax({
					url:"/framework/removeJob",
					type:"post",
					data:{
						jobId:jobId
					},
					success: function(res){
						if(res.success){
							layer.msg("操作成功");
							pageOpt.fetchData();
						}else{
							layer.alert(res.message, {
							  skin: 'layui-layer-molv', //样式类名
							  closeBtn: 0
							});
						}
					}
				})
			})

		});		
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