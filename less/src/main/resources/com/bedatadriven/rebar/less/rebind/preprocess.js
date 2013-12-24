
function dump(arr,level) {
	var dumped_text = "";
	if(!level) level = 0;
	
	//The padding given at the beginning of the line.
	var level_padding = "";
	for(var j=0;j<level+1;j++) level_padding += "    ";
	
	if(typeof(arr) == 'object') { //Array/Hashes/Objects 
		for(var item in arr) {
			var value = arr[item];
			
			if(typeof(value) == 'object') { //If it is an array,
				dumped_text += level_padding + "'" + item + "' ...\n";
				dumped_text += dump(value,level+1);
			} else if(typeof(value)=='function') {
			} else {
				dumped_text += level_padding + "'" + item + "' => ";
				dumped_text += "\"" + value + "\"\n";
			
			}
		}
	} else if(typeof(arr) == 'function') {
		//dumped_text = "<function>";
	} else { //Stings/Chars/Numbers etc.
		dumped_text = "===>"+arr+"<===("+typeof(arr)+")";
	}
	return dumped_text;
}


function visitTree(tree, visitor) {
	if(Array.isArray(tree)) {
		for(i in tree) {
			var item = tree[i];
			visitTree(item, visitor);
		}
	} else if(tree != null && typeof(tree) == 'object') { 
		visitor(tree);
		for(var i in tree) {
			var child = tree[i];
			if(typeof(child) == 'object' && child != null) {
				visitTree(child, visitor);
			}
		}
	}
}
function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}

// \9 is a "CSS hack" specific to Internet Explorer 7, 8, & 9.
// This simply means that the one specific line of CSS ending with a \9; in place of the ; is only valid in IE 7, 8, & 9.
// http://stackoverflow.com/questions/8004765/css-9-in-width-property
function includeIE7_9(node) {
	if(typeof(node.value) === 'string' && endsWith(node.value, "\\9")) {
		node.value = node.value.substring(0, node.value.length-2).trim();
	}
}

// Clsoure chokes on color-stop(a b), expects color-stop(a, b)
// Example: -webkit-linear-gradient(left, color-stop(rgba(0, 0, 0, 0.5) 0%), color-stop(rgba(0, 0, 0, 0.0001) 100%));
// (TODO)
function fixColorStop(node) {
	if(typeof(node.value) === 'string' && node.value.contains('color-stop(')) {
		// tokenize
		
	}
}

function compile(input, userAgent) { 
	var result; 
	var parser = new less.Parser(); 
	parser.parse(input, function(e, tree) { 
		if (e instanceof Object) { 
			throw e; 
		}; 
		
		// we need to clean up some of the browser hacks common
		// in less to get clean input for GSS
		
		//java.lang.System.out.println(dump(tree));
		
		visitTree(tree, includeIE7_9)
		
		
		result = tree.toCSS({compress: false}); 
	});
	return result;
}
