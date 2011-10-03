ver | find "XP" > Nul
REM create index.html files in each folder
if not ErrorLevel 1 (
	update-directory-index.bat
) ELSE (
	update-directory-index.sh
)
	
git add -A
git commit -m "do release"
git push origin gh-pages