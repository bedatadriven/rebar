
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

function preprocessingVisitor(node) {
	
	
	// \9 is a "CSS hack" specific to Internet Explorer 7, 8, & 9.
	// This simply means that the one specific line of CSS ending with a \9; in place of the ; is only valid in IE 7, 8, & 9.
	// http://stackoverflow.com/questions/8004765/css-9-in-width-property
	if(typeof(node.value) === 'string' && endsWith(node.value, "\\9")) {
		node.value = node.value.substring(0, node.value.length-2).trim();
	}
}

function endsWith(str, suffix) {
    return str.indexOf(suffix, str.length - suffix.length) !== -1;
}



function preprocess(tree) { 
	visitTree(tree, preprocessingVisitor)
}
