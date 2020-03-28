function deletion (id){
	swal({
		  title: "Are you sure?",
		  text: "Once deleted, you will not be able to recover this task!",
		  icon: "warning",
		  buttons: true,
		  dangerMode: true,
		})
		.then((OK) => {
		  if (OK) {
			  $.ajax({
				  url:"/delete/"+id,
				  success: function (res){
					  console.log(res);
				  }
			  })
		    swal("Poof! Your task has been deleted!", {
		      icon: "success",
		    }).then((ok)=>{
		    	if(ok){
		    		location.href="/";
		    	}
		    });
		  } else {
		    swal("Your task is safe!");
		  }
		});
	
}