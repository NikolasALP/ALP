@echo off
set REPO_DIR = ".\repo"
for /d /r %REPO_DIR% %%d in (*) do (
echo ^<html^>^<body^>^<h1^>Directory listing^</h1^>^<hr/^>^<pre^>
echo ^<html^>^<body^>^<h1^>Directory listing^</h1^>^<hr/^>^<pre^>
dir /B %%d | findstr -v "^\./$" | findstr -v "^index\.html$" | gawk '{ printf "<a href=\"%%s\">%%s</a><br/>\n",$0,$0 }'
) > %%d/index.html
