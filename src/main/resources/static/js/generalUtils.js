let buttonClone = null;

jQuery.fn.addSpinner = function() {
    buttonClone = this.clone(true);
    this.attr("disabled", true);
    this.unbind();
    const size = $(this).height();
    const spinnerSize = Math.sqrt((size * size) / 2);
    let spinner = $.parseHTML('<div><div class="spinner-border text-light" role="status"></div></div>');
    spinner = $(spinner);
    spinner.children().first().height(spinnerSize);
    spinner.children().first().width(spinnerSize);
    spinner.height(size);
    spinner.width(size);
    this.html(spinner);
};

jQuery.fn.removeSpinner = function () {
    if (buttonClone == null) return;
    this.replaceWith(buttonClone);
    buttonClone = null;
};

jQuery.fn.setInvalid = function () {
    this.addClass("is-invalid");
    this.focusin(() => {
        this.removeClass("is-invalid");
        this.unbind();
    });
};