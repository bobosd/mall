class UploadAdapter {
    constructor(loader) {
        //CKEditor 5 instance
        this.loader = loader;
        console.log(loader);
    }

    upload() {
        return this.loader.file.then(file => {
            return new Promise((resolve, reject) => {
                const data = new FormData();
                data.append("file", file);
                $.ajax({
                    url: "/admin/goods/upload-goods-detail-image",
                    method: "post",
                    processData: false,
                    contentType: false,
                    data: data,
                    success: (res) => {
                        console.log("uploaded", res);
                        if (res.code === 200) {
                            resolve({default: "/admin/goods/img/" + res.data});
                        } else {
                            showErrorAlert("error");
                            reject();
                        }
                    },
                    error: () => {
                        showServerErrorSwal();
                        reject();
                    },
                });
            });
        })
    }

    abort() {
        console.log("Abort");
    }
}