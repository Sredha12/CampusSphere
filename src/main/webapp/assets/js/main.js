/**
 * CampusSphere Vanilla JavaScript
 * Lightweight progressive enhancement: Accessible mobile navigation menu toggle.
 * The application remains fully functional and accessible if JavaScript is disabled.
 */
document.addEventListener('DOMContentLoaded', function () {
    var menuToggle = document.getElementById('menuToggle');
    var primaryNav = document.getElementById('primaryNav');

    if (menuToggle && primaryNav) {
        menuToggle.addEventListener('click', function () {
            var isExpanded = menuToggle.getAttribute('aria-expanded') === 'true';
            var nextState = !isExpanded;

            menuToggle.setAttribute('aria-expanded', String(nextState));
            primaryNav.classList.toggle('nav-open', nextState);
        });

        // Close menu when Escape key is pressed
        document.addEventListener('keydown', function (e) {
            if (e.key === 'Escape' && menuToggle.getAttribute('aria-expanded') === 'true') {
                menuToggle.setAttribute('aria-expanded', 'false');
                primaryNav.classList.remove('nav-open');
                menuToggle.focus();
            }
        });
    }
});
