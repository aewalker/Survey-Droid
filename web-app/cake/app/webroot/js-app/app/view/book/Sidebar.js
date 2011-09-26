Ext.define('Books.view.book.Sidebar', {
    extend: 'Ext.view.View',
    requires: 'Books.store.Books',
    alias: 'widget.booksidebar',
    id: 'sidebar',
    dock: 'left',
    width: 180,
    border: false,
    cls: 'sidebar-list',
    selModel: {
        deselectOnContainerClick: false
    },
    store: '', // uses books store, but to be bound after app ready
    itemSelector: '.product',
    tpl: new Ext.XTemplate(
        '<div class='sidebar-title'>Books</div>',
        '<tpl for='.'>',
            '<div class='product'>{name}</div>',
        '</tpl>'
    )
});