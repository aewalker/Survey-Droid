/*

This file is part of Ext JS 4

Copyright (c) 2011 Sencha Inc

Contact:  http://www.sencha.com/contact

GNU General Public License Usage
This file may be used under the terms of the GNU General Public License version 3.0 as published by the Free Software Foundation and appearing in the file LICENSE included in the packaging of this file.  Please review the following information to ensure the GNU General Public License version 3.0 requirements will be met: http://www.gnu.org/copyleft/gpl.html.

If you are unsure which license is appropriate for your use, please contact the sales department at http://www.sencha.com/contact.

*/
/**
 * @class Ext.chart.Chart
 * @extends Ext.draw.Component
 *
 * The Ext.chart package provides the capability to visualize data.
 * Each chart binds directly to an Ext.data.Store enabling automatic updates of the chart.
 * A chart configuration object has some overall styling options as well as an array of axes
 * and series. A chart instance example could look like:
 *
  <pre><code>
    Ext.create('Ext.chart.Chart', {
        renderTo: Ext.getBody(),
        width: 800,
        height: 600,
        animate: true,
        store: store1,
        shadow: true,
        theme: 'Category1',
        legend: {
            position: 'right'
        },
        axes: [ ...some axes options... ],
        series: [ ...some series options... ]
    });
  </code></pre>
 *
 * In this example we set the `width` and `height` of the chart, we decide whether our series are
 * animated or not and we select a store to be bound to the chart. We also turn on shadows for all series,
 * select a color theme `Category1` for coloring the series, set the legend to the right part of the chart and
 * then tell the chart to render itself in the body element of the document. For more information about the axes and
 * series configurations please check the documentation of each series (Line, Bar, Pie, etc).
 */
Ext.define('Ext.chart.Chart', {

    /* Begin Definitions */

    alias: 'widget.chart',

    extend: 'Ext.draw.Component',
    
    mixins: {
        themeManager: 'Ext.chart.theme.Theme',
        mask: 'Ext.chart.Mask',
        navigation: 'Ext.chart.Navigation'
    },

    requires: [
        'Ext.util.MixedCollection',
        'Ext.data.StoreManager',
        'Ext.chart.Legend',
        'Ext.util.DelayedTask'
    ],

    /* End Definitions */

    // @private
    viewBox: false,

    /**
     * @cfg {String} theme (optional) The name of the theme to be used. A theme defines the colors and
     * other visual displays of tick marks on axis, text, title text, line colors, marker colors and styles, etc.
     * Possible theme values are 'Base', 'Green', 'Sky', 'Red', 'Purple', 'Blue', 'Yellow' and also six category themes
     * 'Category1' to 'Category6'. Default value is 'Base'.
     */

    /**
     * @cfg {Boolean/Object} animate (optional) true for the default animation (easing: 'ease' and duration: 500)
     * or a standard animation config object to be used for default chart animations. Defaults to false.
     */
    animate: false,

    /**
     * @cfg {Boolean/Object} legend (optional) true for the default legend display or a legend config object. Defaults to false.
     */
    legend: false,

    /**
     * @cfg {integer} insetPadding (optional) Set the amount of inset padding in pixels for the chart. Defaults to 10.
     */
    insetPadding: 10,

    /**
     * @cfg {Array} enginePriority
     * Defines the priority order for which Surface implementation to use. The first
     * one supported by the current environment will be used.
     */
    enginePriority: ['Svg', 'Vml'],

    /**
     * @cfg {Object|Boolean} background (optional) Set the chart background. This can be a gradient object, image, or color.
     * Defaults to false for no background.
     *
     * For example, if `background` were to be a color we could set the object as
     *
     <pre><code>
        background: {
            //color string
            fill: '#ccc'
        }
     </code></pre>

     You can specify an image by using:

     <pre><code>
        background: {
            image: 'http://path.to.image/'
        }
     </code></pre>

     Also you can specify a gradient by using the gradient object syntax:

     <pre><code>
        background: {
            gradient: {
                id: 'gradientId',
                angle: 45,
                stops: {
                    0: {
                        color: '#555'
                    }
                    100: {
                        color: '#ddd'
                    }
                }
            }
        }
     </code></pre>
     */
    background: false,

    /**
     * @cfg {Array} gradients (optional) Define a set of gradients that can be used as `fill` property in sprites.
     * The gradients array is an array of objects with the following properties:
     *
     * <ul>
     * <li><strong>id</strong> - string - The unique name of the gradient.</li>
     * <li><strong>angle</strong> - number, optional - The angle of the gradient in degrees.</li>
     * <li><strong>stops</strong> - object - An object with numbers as keys (from 0 to 100) and style objects
     * as values</li>
     * </ul>
     *

     For example:

     <pre><code>
        gradients: [{
            id: 'gradientId',
            angle: 45,
            stops: {
                0: {
                    color: '#555'
                },
                100: {
                    color: '#ddd'
                }
            }
        },  {
            id: 'gradientId2',
            angle: 0,
            stops: {
                0: {
                    color: '#590'
                },
                20: {
                    color: '#599'
                },
                100: {
                    color: '#ddd'
                }
            }
        }]
     </code></pre>

     Then the sprites can use `gradientId` and `gradientId2` by setting the fill attributes to those ids, for example:

     <pre><code>
        sprite.setAttributes({
            fill: 'url(#gradientId)'
        }, true);
     </code></pre>

     */


    constructor: function(config) {
        var me = this,
            defaultAnim;
        me.initTheme(config.theme || me.theme);
        if (me.gradients) {
            Ext.apply(config, { gradients: me.gradients });
        }
        if (me.background) {
            Ext.apply(config, { background: me.background });
        }
        if (config.animate) {
            defaultAnim = {
                easing: 'ease',
                duration: 500
            };
            if (Ext.isObject(config.animate)) {
                config.animate = Ext.applyIf(config.animate, defaultAnim);
            }
            else {
                config.animate = defaultAnim;
            }
        }
        me.mixins.mask.constructor.call(me, config);
        me.mixins.navigation.constructor.call(me, config);
        me.callParent([config]);
    },

    initComponent: function() {
        var me = this,
            axes,
            series;
        me.callParent();
        me.addEvents(
            'itemmousedown',
            'itemmouseup',
            'itemmouseover',
            'itemmouseout',
            'itemclick',
            'itemdoubleclick',
            'itemdragstart',
            'itemdrag',
            'itemdragend',
            /**
                 * @event beforerefresh
                 * Fires before a refresh to the chart data is called.  If the beforerefresh handler returns
                 * <tt>false</tt> the {@link #refresh} action will be cancelled.
                 * @param {Chart} this
                 */
            'beforerefresh',
            /**
                 * @event refresh
                 * Fires after the chart data has been refreshed.
                 * @param {Chart} this
                 */
            'refresh'
        );
        Ext.applyIf(me, {
            zoom: {
                width: 1,
                height: 1,
                x: 0,
                y: 0
            }
        });
        me.maxGutter = [0, 0];
        me.store = Ext.data.StoreManager.lookup(me.store);
        axes = me.axes;
        me.axes = Ext.create('Ext.util.MixedCollection', false, function(a) { return a.position; });
        if (axes) {
            me.axes.addAll(axes);
        }
        series = me.series;
        me.series = Ext.create('Ext.util.MixedCollection', false, function(a) { return a.seriesId || (a.seriesId = Ext.id(null, 'ext-chart-series-')); });
        if (series) {
            me.series.addAll(series);
        }
        if (me.legend !== false) {
            me.legend = Ext.create('Ext.chart.Legend', Ext.applyIf({chart:me}, me.legend));
        }

        me.on({
            mousemove: me.onMouseMove,
            mouseleave: me.onMouseLeave,
            mousedown: me.onMouseDown,
            mouseup: me.onMouseUp,
            scope: me
        });
    },

    // @private overrides the component method to set the correct dimensions to the chart.
    afterComponentLayout: function(width, height) {
        var me = this;
        if (Ext.isNumber(width) && Ext.isNumber(height)) {
            me.curWidth = width;
            me.curHeight = height;
            me.redraw(true);
        }
        this.callParent(arguments);
    },

    /**
     * Redraw the chart. If animations are set this will animate the chart too.
     * @cfg {boolean} resize Optional flag which changes the default origin points of the chart for animations.
     */
    redraw: function(resize) {
        var me = this,
            chartBBox = me.chartBBox = {
                x: 0,
                y: 0,
                height: me.curHeight,
                width: me.curWidth
            },
            legend = me.legend;
        me.surface.setSize(chartBBox.width, chartBBox.height);
        // Instantiate Series and Axes
        me.series.each(me.initializeSeries, me);
        me.axes.each(me.initializeAxis, me);
        //process all views (aggregated data etc) on stores
        //before rendering.
        me.axes.each(function(axis) {
            axis.processView();
        });
        me.axes.each(function(axis) {
            axis.drawAxis(true);
        });

        // Create legend if not already created
        if (legend !== false) {
            legend.create();
        }

        // Place axes properly, including influence from each other
        me.alignAxes();

        // Reposition legend based on new axis alignment
        if (me.legend !== false) {
            legend.updatePosition();
        }

        // Find the max gutter
        me.getMaxGutter();

        // Draw axes and series
        me.resizing = !!resize;

        me.axes.each(me.drawAxis, me);
        me.series.each(me.drawCharts, me);
        me.resizing = false;
    },

    // @private set the store after rendering the chart.
    afterRender: function() {
        var ref,
            me = this;
        this.callParent();

        if (me.categoryNames) {
            me.setCategoryNames(me.categoryNames);
        }

        if (me.tipRenderer) {
            ref = me.getFunctionRef(me.tipRenderer);
            me.setTipRenderer(ref.fn, ref.scope);
        }
        me.bindStore(me.store, true);
        me.refresh();
    },

    // @private get x and y position of the mouse cursor.
    getEventXY: function(e) {
        var me = this,
            box = this.surface.getRegion(),
            pageXY = e.getXY(),
            x = pageXY[0] - box.left,
            y = pageXY[1] - box.top;
        return [x, y];
    },

    // @private wrap the mouse down position to delegate the event to the series.
    onClick: function(e) {
        var me = this,
            position = me.getEventXY(e),
            item;

        // Ask each series if it has an item corresponding to (not necessarily exactly
        // on top of) the current mouse coords. Fire itemclick event.
        me.series.each(function(series) {
            if (Ext.draw.Draw.withinBox(position[0], position[1], series.bbox)) {
                if (series.getItemForPoint) {
                    item = series.getItemForPoint(position[0], position[1]);
                    if (item) {
                        series.fireEvent('itemclick', item);
                    }
                }
            }
        }, me);
    },

    // @private wrap the mouse down position to delegate the event to the series.
    onMouseDown: function(e) {
        var me = this,
            position = me.getEventXY(e),
            item;

        if (me.mask) {
            me.mixins.mask.onMouseDown.call(me, e);
        }
        // Ask each series if it has an item corresponding to (not necessarily exactly
        // on top of) the current mouse coords. Fire mousedown event.
        me.series.each(function(series) {
            if (Ext.draw.Draw.withinBox(position[0], position[1], series.bbox)) {
                if (series.getItemForPoint) {
                    item = series.getItemForPoint(position[0], position[1]);
                    if (item) {
                        series.fireEvent('itemmousedown', item);
                    }
                }
            }
        }, me);
    },

    // @private wrap the mouse up event to delegate it to the series.
    onMouseUp: function(e) {
        var me = this,
            position = me.getEventXY(e),
            item;

        if (me.mask) {
            me.mixins.mask.onMouseUp.call(me, e);
        }
        // Ask each series if it has an item corresponding to (not necessarily exactly
        // on top of) the current mouse coords. Fire mousedown event.
        me.series.each(function(series) {
            if (Ext.draw.Draw.withinBox(position[0], position[1], series.bbox)) {
                if (series.getItemForPoint) {
                    item = series.getItemForPoint(position[0], position[1]);
                    if (item) {
                        series.fireEvent('itemmouseup', item);
                    }
                }
            }
        }, me);
    },

    // @private wrap the mouse move event so it can be delegated to the series.
    onMouseMove: function(e) {
        var me = this,
            position = me.getEventXY(e),
            item, last, storeItem, storeField;

        if (me.mask) {
            me.mixins.mask.onMouseMove.call(me, e);
        }
        // Ask each series if it has an item corresponding to (not necessarily exactly
        // on top of) the current mouse coords. Fire itemmouseover/out events.
        me.series.each(function(series) {
            if (Ext.draw.Draw.withinBox(position[0], position[1], series.bbox)) {
                if (series.getItemForPoint) {
                    item = series.getItemForPoint(position[0], position[1]);
                    last = series._lastItemForPoint;
                    storeItem = series._lastStoreItem;
                    storeField = series._lastStoreField;


                    if (item !== last || item && (item.storeItem != storeItem || item.storeField != storeField)) {
                        if (last) {
                            series.fireEvent('itemmouseout', last);
                            delete series._lastItemForPoint;
                            delete series._lastStoreField;
                            delete series._lastStoreItem;
                        }
                        if (item) {
                            series.fireEvent('itemmouseover', item);
                            series._lastItemForPoint = item;
                            series._lastStoreItem = item.storeItem;
                            series._lastStoreField = item.storeField;
                        }
                    }
                }
            } else {
                last = series._lastItemForPoint;
                if (last) {
                    series.fireEvent('itemmouseout', last);
                    delete series._lastItemForPoint;
                    delete series._lastStoreField;
                    delete series._lastStoreItem;
                }
            }
        }, me);
    },

    // @private handle mouse leave event.
    onMouseLeave: function(e) {
        var me = this;
        if (me.mask) {
            me.mixins.mask.onMouseLeave.call(me, e);
        }
        me.series.each(function(series) {
            delete series._lastItemForPoint;
        });
    },

    // @private buffered refresh for when we update the store
    delayRefresh: function() {
        var me = this;
        if (!me.refreshTask) {
            me.refreshTask = Ext.create('Ext.util.DelayedTask', me.refresh, me);
        }
        me.refreshTask.delay(me.refreshBuffer);
    },

    // @private
    refresh: function() {
        var me = this;
        if (me.rendered && me.curWidth != undefined && me.curHeight != undefined) {
            if (me.fireEvent('beforerefresh', me) !== false) {
                me.redraw();
                me.fireEvent('refresh', me);
            }
        }
    },

    /**
     * Changes the data store bound to this chart and refreshes it.
     * @param {Store} store The store to bind to this chart
     */
    bindStore: function(store, initial) {
        var me = this;
        if (!initial && me.store) {
            if (store !== me.store && me.store.autoDestroy) {
                me.store.destroy();
            }
            else {
                me.store.un('datachanged', me.refresh, me);
                me.store.un('add', me.delayRefresh, me);
                me.store.un('remove', me.delayRefresh, me);
                me.store.un('update', me.delayRefresh, me);
                me.store.un('clear', me.refresh, me);
            }
        }
        if (store) {
            store = Ext.data.StoreManager.lookup(store);
            store.on({
                scope: me,
                datachanged: me.refresh,
                add: me.delayRefresh,
                remove: me.delayRefresh,
                update: me.delayRefresh,
                clear: me.refresh
            });
        }
        me.store = store;
        if (store && !initial) {
            me.refresh();
        }
    },

    // @private Create Axis
    initializeAxis: function(axis) {
        var me = this,
            chartBBox = me.chartBBox,
            w = chartBBox.width,
            h = chartBBox.height,
            x = chartBBox.x,
            y = chartBBox.y,
            themeAttrs = me.themeAttrs,
            config = {
                chart: me
            };
        if (themeAttrs) {
            config.axisStyle = Ext.apply({}, themeAttrs.axis);
            config.axisLabelLeftStyle = Ext.apply({}, themeAttrs.axisLabelLeft);
            config.axisLabelRightStyle = Ext.apply({}, themeAttrs.axisLabelRight);
            config.axisLabelTopStyle = Ext.apply({}, themeAttrs.axisLabelTop);
            config.axisLabelBottomStyle = Ext.apply({}, themeAttrs.axisLabelBottom);
            config.axisTitleLeftStyle = Ext.apply({}, themeAttrs.axisTitleLeft);
            config.axisTitleRightStyle = Ext.apply({}, themeAttrs.axisTitleRight);
            config.axisTitleTopStyle = Ext.apply({}, themeAttrs.axisTitleTop);
            config.axisTitleBottomStyle = Ext.apply({}, themeAttrs.axisTitleBottom);
        }
        switch (axis.position) {
            case 'top':
                Ext.apply(config, {
                    length: w,
                    width: h,
                    x: x,
                    y: y
                });
            break;
            case 'bottom':
                Ext.apply(config, {
                    length: w,
                    width: h,
                    x: x,
                    y: h
                });
            break;
            case 'left':
                Ext.apply(config, {
                    length: h,
                    width: w,
                    x: x,
                    y: h
                });
            break;
            case 'right':
                Ext.apply(config, {
                    length: h,
                    width: w,
                    x: w,
                    y: h
                });
            break;
        }
        if (!axis.chart) {
            Ext.apply(config, axis);
            axis = me.axes.replace(Ext.createByAlias('axis.' + axis.type.toLowerCase(), config));
        }
        else {
            Ext.apply(axis, config);
        }
    },


    /**
     * @private Adjust the dimensions and positions of each axis and the chart body area after accounting
     * for the space taken up on each side by the axes and legend.
     */
    alignAxes: function() {
        var me = this,
            axes = me.axes,
            legend = me.legend,
            edges = ['top', 'right', 'bottom', 'left'],
            chartBBox,
            insetPadding = me.insetPadding,
            insets = {
                top: insetPadding,
                right: insetPadding,
                bottom: insetPadding,
                left: insetPadding
            };

        function getAxis(edge) {
            var i = axes.findIndex('position', edge);
            return (i < 0) ? null : axes.getAt(i);
        }

        // Find the space needed by axes and legend as a positive inset from each edge
        Ext.each(edges, function(edge) {
            var isVertical = (edge === 'left' || edge === 'right'),
                axis = getAxis(edge),
                bbox;

            // Add legend size if it's on this edge
            if (legend !== false) {
                if (legend.position === edge) {
                    bbox = legend.getBBox();
                    insets[edge] += (isVertical ? bbox.width : bbox.height) + insets[edge];
                }
            }

            // Add axis size if there's one on this edge only if it has been
            //drawn before.
            if (axis && axis.bbox) {
                bbox = axis.bbox;
                insets[edge] += (isVertical ? bbox.width : bbox.height);
            }
        });
        // Build the chart bbox based on the collected inset values
        chartBBox = {
            x: insets.left,
            y: insets.top,
            width: me.curWidth - insets.left - insets.right,
            height: me.curHeight - insets.top - insets.bottom
        };
        me.chartBBox = chartBBox;

        // Go back through each axis and set its length and position based on the
        // corresponding edge of the chartBBox
        axes.each(function(axis) {
            var pos = axis.position,
                isVertical = (pos === 'left' || pos === 'right');

            axis.x = (pos === 'right' ? chartBBox.x + chartBBox.width : chartBBox.x);
            axis.y = (pos === 'top' ? chartBBox.y : chartBBox.y + chartBBox.height);
            axis.width = (isVertical ? chartBBox.width : chartBBox.height);
            axis.length = (isVertical ? chartBBox.height : chartBBox.width);
        });
    },

    // @private initialize the series.
    initializeSeries: function(series, idx) {
        var me = this,
            themeAttrs = me.themeAttrs,
            seriesObj, markerObj, seriesThemes, st,
            markerThemes, colorArrayStyle = [],
            i = 0, l,
            config = {
                chart: me,
                seriesId: series.seriesId
            };
        if (themeAttrs) {
            seriesThemes = themeAttrs.seriesThemes;
            markerThemes = themeAttrs.markerThemes;
            seriesObj = Ext.apply({}, themeAttrs.series);
            markerObj = Ext.apply({}, themeAttrs.marker);
            config.seriesStyle = Ext.apply(seriesObj, seriesThemes[idx % seriesThemes.length]);
            config.seriesLabelStyle = Ext.apply({}, themeAttrs.seriesLabel);
            config.markerStyle = Ext.apply(markerObj, markerThemes[idx % markerThemes.length]);
            if (themeAttrs.colors) {
                config.colorArrayStyle = themeAttrs.colors;
            } else {
                colorArrayStyle = [];
                for (l = seriesThemes.length; i < l; i++) {
                    st = seriesThemes[i];
                    if (st.fill || st.stroke) {
                        colorArrayStyle.push(st.fill || st.stroke);
                    }
                }
                if (colorArrayStyle.length) {
                    config.colorArrayStyle = colorArrayStyle;
                }
            }
            config.seriesIdx = idx;
        }
        if (series instanceof Ext.chart.series.Series) {
            Ext.apply(series, config);
        } else {
            Ext.applyIf(config, series);
            series = me.series.replace(Ext.createByAlias('series.' + series.type.toLowerCase(), config));
        }
        if (series.initialize) {
            series.initialize();
        }
    },

    // @private
    getMaxGutter: function() {
        var me = this,
            maxGutter = [0, 0];
        me.series.each(function(s) {
            var gutter = s.getGutters && s.getGutters() || [0, 0];
            maxGutter[0] = Math.max(maxGutter[0], gutter[0]);
            maxGutter[1] = Math.max(maxGutter[1], gutter[1]);
        });
        me.maxGutter = maxGutter;
    },

    // @private draw axis.
    drawAxis: function(axis) {
        axis.drawAxis();
    },

    // @private draw series.
    drawCharts: function(series) {
        series.triggerafterrender = false;
        series.drawSeries();
        if (!this.animate) {
            series.fireEvent('afterrender');
        }
    },

    // @private remove gently.
    destroy: function() {
        this.surface.destroy();
        this.bindStore(null);
        this.callParent(arguments);
    }
});

