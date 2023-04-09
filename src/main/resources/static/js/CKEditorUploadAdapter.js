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
                    url: "/admin/ckeditor/upload",
                    method: "post",
                    processData: false,
                    contentType: false,
                    data: data,
                    success: (res) => {
                        if (res.code === 200) {
                            resolve({default: res.data.url});
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

    }
}