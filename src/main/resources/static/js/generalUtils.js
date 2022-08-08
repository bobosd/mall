let buttonClone = null;

jQuery.fn.addSpinner = function() {
    buttonClone = this.clone(true);
    this.attr("disabled", true);
    this.unbind();
    let size = $(this).height();
    let spinner = $.parseHTML('<div class="spinner-border text-light" role="status"></div>');
    spinner = $(spinner);
    spinner.height(size);
    spinner.width(size);
    this.html(spinner);
};

jQuery.fn.removeSpinner = function () {
    if (buttonClone == null) return;
    this.replaceWith(buttonClone);
    buttonClone = null;
};