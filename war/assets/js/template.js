function ElementTpl(str) {
    var pattern, template, varHolders, idxHolders, tmp, tmp2;
    pattern = new RegExp();
    pattern.compile("#\\{[^\\}]+\\}", "g");
    template = str;

    varHolders = [];
    idxHolders = [];

    $.each(template.match(pattern), function(i, holder) {
        tmp = holder.replace(/[#\{\}]/g, "");
        tmp2 = parseInt(tmp);
        if (isNaN(tmp2)) {
            varHolders.push(tmp);
        } else {
            idxHolders.push(tmp2);
        }
    });

    /*
     * Fills out the template using the supplied arguments Usage: temp.fill(arg)
     * where arg is an array or object
     */
    this.fill = function(arg) {
        if (arg instanceof Array) {
            tmp = template;
            $.each(idxHolders, function(i, idx) {
                tmp = tmp.replace("#{" + idx + "}", arg[idx]);
            });
            return $(tmp);
        } else {
            tmp = str;
            $.each(varHolders, function(i, prop) {
                tmp = tmp.replace("#{" + prop + "}", drillDeepProps(arg, prop
                        .split(".")));
            });
            return $(tmp);
        }
    }

    function drillDeepProps(arg, props) { // FIXME
        var retVal = arg;
        for ( var i in props) {
            retVal = retVal[props[i]];
        }
        return retVal;
    }
}