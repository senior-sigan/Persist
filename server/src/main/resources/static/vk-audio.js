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
    });
})();
