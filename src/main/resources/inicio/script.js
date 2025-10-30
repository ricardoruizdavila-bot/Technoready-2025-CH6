$(document).ready(function() {
  $(".offer-btn").click(function() {
    $("#offer-form").toggle();
  });

  $("#offer-form").submit(function(event) {
    event.preventDefault();

    let name   = $("#name-input").val();
    let email  = $("#email-input").val();
    let amount = parseFloat($("#amount-input").val()).toFixed(2);
    let itemId = $("#item-id-input").val(); // <-- agregado

    if (!name || !email || !amount || !itemId) {
      alert("Please fill out all fields!");
      return;
    }

    $.ajax({
      url: "/api/v1/offers",        // <-- corregido
      method: "POST",
      data: {
        itemId: itemId,
        name: name,
        email: email,
        amount: amount
      },
      success: function(response) {
        alert("Your offer has been submitted!");
        $("#offer-form").trigger("reset").hide();
        // opcional: window.location.href = "/ui/offers";
      },
      error: function(jqXHR) {
        let msg = "Error submitting offer";
        try {
          const obj = JSON.parse(jqXHR.responseText);
          if (obj && obj.message) msg += ": " + obj.message;
        } catch(_) {}
        alert(msg);
      }
    });
  });
});
