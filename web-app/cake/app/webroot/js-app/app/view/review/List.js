Ext.define('Books.view.review.List', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.reviewlist',

    border: false,
    flex: 2,
    id: 'test',

    layout: 'card',

    dockedItems: Ext.create('Books.view.Header', {
        html: 'Reviews'
    }),

    items: {
        xtype: 'dataview',
        id: 'reviews',
        border: false,
        cls: 'review-list',
        autoScroll: true,
        store: 'Books.store.Review', // why no 's' at end?
        itemSelector: '.review',
        tpl: new Ext.XTemplate(
            '<tpl for='.'>',
                '<div class='review {[xindex === 1 ? 'first-review' : '']}'>',
                    '<div class='title'>{title} {[this.stars(values)]}</div>',
                    '<div class='author'>By <span>{author}</span> - {date}</div>',
                    '<div class='comment'>{comment}</div>',
                '</div>',
            '</tpl>',
            {
                stars: function(values) {
                    var res = '';
                    for (var i = 0; i < values.rating; i++) {
                        res += '<img src='./resources/images/star.' + ((Ext.isIE6) ? 'gif' : 'png') + '' />';
                    }

                    while (i < 5) {
                        res += '<img src='./resources/images/star_no.' + ((Ext.isIE6) ? 'gif' : 'png') + '' />';
                        i++;
                    }
                    return res;
                }
            }
        )
    },

    /* Bind a store to this dataview
     */
    bind: function(record, store) {
        store.loadData(record.data.reviews || []);
        Ext.getCmp('reviews').bindStore(store);
    }
});