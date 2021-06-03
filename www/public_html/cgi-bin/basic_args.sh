#!/bin/bash

echo "Content-type: text/html"
echo ""

echo '<html xmlns="http://www.w3.org/1999/xhtml" lang="en-US" xml:lang="en-US">'
echo '<head>'
echo '<title>Environment</title>'
echo '<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />'
echo '</head>'
echo '<body>'
echo '<pre>'
/usr/bin/env
echo '</pre>'
echo '</body>'
echo '</html>'

exit 0