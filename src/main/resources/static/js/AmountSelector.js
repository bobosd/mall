// noinspection EqualityComparisonWithCoercionJS

function bindAmountInputFilterEvent() {
    const input = $('.input-buy-amount');
    input.each((i, e) => {
        e.oldValue = '1';
    }); //设置归档默认值

    //值输入前
    input.on("keydown", (e) => {
        const inputValue = e.key;
        const valueBeforeInput = e.target.value;

        //如果不是数字并且非退格键，那么不允许输入并且归档
        if (inputValue !== "Backspace" && !Validator.isNumeric(inputValue)) {
            e.preventDefault();
            e.target.value = e.target.oldValue;
        }

        //如果是退格键但是输入框值已经小于等于1位数，那么不允许退格并且归档
        if (inputValue === "Backspace" && valueBeforeInput.length <= 1) {
            //如果按住退格键，一次性删除了多位数，更新归档为1位数
            if (valueBeforeInput.length === 1) {
                e.target.oldValue = valueBeforeInput.charAt(0);
            }
            e.preventDefault();
            e.target.value = e.target.oldValue;
        }
    });

    //值输入后
    input.on("keyup change", (e) => {
        let currentValue = e.target.value;
        //当前值为 "0"，那么归档
        if (currentValue === "0") {
            e.target.value = e.target.oldValue;
            return;
        }

        if (Validator.isNumeric(currentValue) && currentValue.length > 1) {
            e.target.value = 9;
            if (e.target.oldValue !== 9) {
                e.target.oldValue = 9;
                changeMadeCallBack(e.target);
            }
            return;
        }

        //如果输入框的值是数字并且不等于 "0"，那么更新归档的值
        if (Validator.isPositiveNumber(currentValue) && e.target.oldValue != currentValue) {
            e.target.oldValue = e.target.value;
            changeMadeCallBack(e.target);
        }
    });
}

function bindGoodsAmountOptionsEvent() {
    $(".add-buy-amount").click((e) => {
        const input = $(e.target)
            .closest(".choose-amount")
            .find(".input-buy-amount");
        const amount = parseInt(input.val()) + 1;
        if (amount > 9) {
            return;
        }
        input.val(amount);
        console.log(input.get(0), input.val());
        changeMadeCallBack(input.get(0));
    });

    $(".reduce-buy-amount").click((e) => {
        const input = $(e.target)
            .closest(".choose-amount")
            .find(".input-buy-amount");
        const amount = parseInt(input.val()) - 1;
        if (amount === 0) {
            return;
        }
        input.val(amount);
        changeMadeCallBack(input.get(0));
    });

    $(".input-buy-amount").focusin((e) => {
        e.target.select();
    });
}

function changeMadeCallBack(DOM) {
    if (DOM.afterChangeMade != null) {
        DOM.afterChangeMade();
    }
}

bindAmountInputFilterEvent();
bindGoodsAmountOptionsEvent();