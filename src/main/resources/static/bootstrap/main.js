$(document).ready( function() {

    $("#btnGenerate").on("click", generateSchema);

    setTimeout(function() {
            $(".hideonload").hide(100);
            $(".showonload").show(150);
        }, 300);
});


function generateSchema() {
    $("#alertsDiv").empty();
    $("#btnGenerate").prop("disabled", true);
    $("#jsonInput").prop("readonly", true);



    let sInputJSON = $("#jsonInput").val();
    console.log("json: " + sInputJSON);

    if (sInputJSON && sInputJSON != "") {
        let jsInputJSON = JSON.stringify(sInputJSON);
        // TODO: add try/catch



        $.ajax(
            {
                url: "create/json/inner",
                data: sInputJSON,
                contentType: "application/json",
                method: "POST"
            }
            )
            .done(function( data ) {
                $("#jsonSchema").empty();

                if (data && data.errors && data.errors.length==0) {
                    let jsonSchema = JSON.parse(data.data);
                    $("#jsonSchema").val(JSON.stringify(jsonSchema, null, 4));
                }
                else {
                    // handle errors
                    console.log ("Some errors occurred");

                    let sError = "Unknown error";

                    if (data && data.errors && data.errors.length>0) {
                        sError = data.errors;
                    }

                    $("#alertsDiv").append(`
<div class="alert alert-warning alert-dismissible fade show" role="alert">
  <strong>Error: </strong>${sError}
  <button type="button" class="close" data-dismiss="alert" aria-label="Close">
    <span aria-hidden="true">&times;</span>
  </button>
</div>
`);

                }

                $("#jsonInput").prop("readonly", false);
                $("#btnGenerate").prop("disabled", false);
            })
            .fail(function() {
              $('#modalSpin').hide();
              $('#modalAlerts .alert-danger').empty().append("Error occurred");
              $('#modalAlerts .alert-danger').show();
            });
    }
    else {
        $('#dlgEmptyJSON').modal({'backdrop':'static'});
        $("#jsonInput").prop("readonly", false);
        $("#btnGenerate").prop("disabled", false);
    }
}