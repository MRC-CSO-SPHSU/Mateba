/**
 * Copyright (C) 1999 CERN - European Organization for Nuclear Research.
 * Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose
 * is hereby granted without fee, provided that the above copyright notice appear in all copies and
 * that both that copyright notice and this permission notice appear in supporting documentation.
 * CERN makes no representations about the suitability of this software for any purpose.
 * It is provided "as is" without expressed or implied warranty.
 */
package cern.clhep;

/**
 * High Energy Physics coherent Physical Constants based on Geant4 (a simulation toolkit for HEP).
 */
@SuppressWarnings("unused")
public final class PhysicalConstants {
    /**
     * Little trick to allow for "aliasing", that is, renaming this class. Normally you would write
     *
     * <pre>
     * PhysicalConstants.twopi;
     * PhysicalConstants.c_light;
     * PhysicalConstants.h_Planck;
     * </pre>
     *
     * Since this class has only static methods, but no instance methods you can also shorten the name
     * "PhysicalConstants" to a name that better suits you, for example "P".
     *
     * <pre>
     * PhysicalConstants P = PhysicalConstants.physicalConstants; // kind of &quot;alias&quot;
     * P.twopi;
     * P.c_light;
     * P.h_Planck;
     * </pre>
     */

    public static final double pi = Math.PI;

    public static final double twopi = 2 * pi;
    public static final double h_Planck = 6.6260755e-34 * Units.joule * Units.s;
    public static final double c_light = 2.99792458e+8 * Units.m / Units.s;
    public static final double hbar_Planck = h_Planck / twopi;
    public static final double hbarc = hbar_Planck * c_light;
    public static final double hbarc_squared = hbarc * hbarc;
    public static final double electron_mass_c2 = 0.51099906 * Units.MeV;
    public static final double electron_Compton_length = hbarc / electron_mass_c2;
    public static final double halfpi = pi / 2;
    public static final double pi2 = pi * pi;

    public static final double Avogadro = 6.0221367e+23 / Units.mole;


    public static final double c_squared = c_light * c_light;
    public static final double amu_c2 = 931.49432 * Units.MeV;
    public static final double amu = amu_c2 / c_squared;



    public static final double electron_charge = -Units.eplus;
    public static final double e_squared = Units.eplus * Units.eplus;


    public static final double proton_mass_c2 = 938.27231 * Units.MeV;
    public static final double neutron_mass_c2 = 939.56563 * Units.MeV;

    public static final double mu0 = 4 * pi * 1.e-7 * Units.henry / Units.m;
    public static final double epsilon0 = 1. / (c_squared * mu0);

    public static final double elm_coupling = e_squared / (4 * pi * epsilon0);
    public static final double fine_structure_const = elm_coupling / hbarc;
    public static final double Bohr_radius = electron_Compton_length / fine_structure_const;
    public static final double classic_electr_radius = elm_coupling / electron_mass_c2;
    public static final double alpha_rcl2 = fine_structure_const * classic_electr_radius * classic_electr_radius;

    public static final double twopi_mc2_rcl2 = twopi * electron_mass_c2 * classic_electr_radius * classic_electr_radius;

    public static final double k_Boltzmann = 8.617385e-11 * Units.MeV / Units.kelvin;

    public static final double STP_Temperature = 273.15 * Units.kelvin;

    public static final double STP_Pressure = Units.atmosphere;

    public static final double kGasThreshold = 10. * Units.mg / Units.cm3;

    public static final double universe_mean_density = 1.e-25 * Units.g / Units.cm3;

    private PhysicalConstants(){
        throw new AssertionError("Do not instantiate.");
    }
}
