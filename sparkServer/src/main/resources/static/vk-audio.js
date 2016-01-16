(function() {
    'use strict';

    $(document).ready(function() {
        $('#vk-audio-download').click(function() {
            var href = $('#vk-audio-input').val();
            var id = href.split('wall')[1];
            if (id && id.length !== 0) {
                window.open('/vk/'+id);
            }
        });

        buildCoversGallery($('#covers'));
    });

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
                        link.tagret = '_blank';
                        var image = new Image();
                        image.src = post['cover'];
                        link.appendChild(image);
                        covers.append(link);
                    }
                });
            }
        });
    }
})();
