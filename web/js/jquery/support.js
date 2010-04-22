/**
 * Funkcia vrati nazov css vlastnosti, ak ju prehliadac podporuje.
 * Berie do uvahy aj prefixovane vlastnosti jednotlivych prehliadacov.
 */
var getStyleProperty = (function() {

  var prefixes = ['Moz', 'Webkit', 'Khtml', 'O', 'Ms'];

  function getStyleProperty(propName, element) {
    element = element || document.documentElement;
    var style = element.style,
        prefixed;

    // test standard property first
    if (typeof style[propName] == 'string') return propName;

    // capitalize
    propName = propName.charAt(0).toUpperCase() + propName.slice(1);

    // test vendor specific properties
    for (var i=0, l=prefixes.length; i<l; i++) {
      prefixed = prefixes[i] + propName;
      if (typeof style[prefixed] == 'string') return prefixed;
    }

	return undefined;
  }

  return getStyleProperty;
})();


/**
 * Rozsirenie jQuery.support o kontrolu niektorych vlastnosti.
 */
(function($) {

	// CSS properties
	jQuery.extend(jQuery.support, {
		boxSizing: typeof(getStyleProperty('boxSizing')) === 'string'
	});

})(jQuery);
