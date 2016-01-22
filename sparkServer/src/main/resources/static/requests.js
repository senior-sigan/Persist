(function($) {
    'use strict';
    buildCoversGallery($('#covers'));

    function buildCoversGallery(covers) {
        $.ajax({
            url: 'requests.json',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                data.forEach(function(post) {
                    if (post.cover != undefined && post.cover != '') {
                        var link = document.createElement('a');
                        link.href = post.url;
                        link.target = '_blank';
                        var image = new Image();
                        image.src = post['cover'];
                        link.appendChild(image);
                        covers.append(link);
                    }
                });
            }
        });
    }
})(jQuery);