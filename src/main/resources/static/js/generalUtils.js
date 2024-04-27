let buttonClone = null;

jQuery.fn.addSpinner = function() {
    buttonClone = this.clone(true);
    this.attr("disabled", true);
    this.unbind();
    const height = this.height();
    const width = this.width();
    const maxSize = width < height ? width : height;
    const spinnerSize = Math.sqrt((maxSize * maxSize) / 2);
    let spinner = $.parseHTML('<div><div class="spinner-border text-light" role="status"></div></div>');//bootstrap spinner
    spinner = $(spinner);
    spinner.children().first().height(spinnerSize);
    spinner.children().first().width(spinnerSize);
    //spinner.height(maxSize);
    //spinner.width(maxSize);
    this.html(spinner);
};

jQuery.fn.removeSpinner = function () {
    if (buttonClone == null) return;
    this.replaceWith(buttonClone);
    buttonClone = null;
};

jQuery.fn.setInvalid = function () {
    this.removeClass("is-valid");
    this.addClass("is-invalid");
    this.focusin(() => {
        this.removeClass("is-invalid");
        this.unbind();
    });
    this.click(() => {
        this.removeClass("is-invalid");
        this.unbind();
    });
};

jQuery.fn.setInvalidWithFeedBack = function (invalidMessage) {
    let nextDOM = this.next();
    for (let i = 0; i < 2; i++) {
        if (nextDOM.is("div") && nextDOM.hasClass("invalid-feedback")) {
            nextDOM.text(invalidMessage);
        }
        nextDOM = nextDOM.next();
    }
    this.removeClass("is-valid");
    this.addClass("is-invalid");
};

jQuery.fn.setValid = function () {
    this.removeClass("is-invalid");
    this.addClass("is-valid");
};

function getCurrentUrl() {
    return window.location.origin + window.location.pathname;
}

function getBaseUrl() {
    return window.location.origin;
}

function parseNumberToEuro(number) {
    const fixedNumber = Number.parseFloat(number).toFixed(2);
    return fixedNumber.replace(".", ",") + '€';
}

function calcPrice(price, count, totalPrice) {
    return (price * count * 10 + totalPrice * 10) / 10;
}

class Validator {
    static isNumeric(str) {
        const regEx = new RegExp("^(-)?([0-9])+((.|\,)?[0-9]+)?$");
        return regEx.test(str);
    }

    static isInteger(str) {
        const regEx = new RegExp("^(-)?[0-9]+$");
        return regEx.test(str);
    }

    static isPositiveNumber(str) {
        return this.isNumeric(str) && !str.startsWith("-");
    }

    //allow english letters with accent, chinese characters
    static onlyLetters(str) {
        const regEx = new RegExp("[a-zA-ZÀ-ÿ\u4e00-\u9fa5\\s\\d]+");
        return regEx.test(str);
    }
}